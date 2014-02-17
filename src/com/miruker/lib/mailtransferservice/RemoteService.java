/**
 * 
 */
package com.miruker.lib.mailtransferservice;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.miruker.lib.mailtransferservice.Utils.DateUtils;
import com.miruker.lib.mailtransferservice.Utils.ImapUtils;
import com.miruker.lib.mailtransferservice.Utils.MailUtils;
import com.miruker.lib.mailtransferservice.Utils.NotificationUtility;

/**
 * @author katsuki-nakatani
 *
 */
public class RemoteService extends Service {

	private static final String TAG = "REMOTE_SERVICE";
	
	private static final String DATE_FORMAT = "yyyy/MM/dd";

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO 自動生成されたメソッド・スタブ
		return mBinder;
	}
	
	@Override	
	public void onCreate() {
		super.onCreate();		
	};
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private final IMailTransfer.Stub mBinder = new IMailTransfer.Stub() {
		
		private boolean mIsTaskRunning = false;
		private boolean mIsCanceld = false;
		
		@Override
		public int getAppVersion() throws RemoteException {
			PackageManager pm = getPackageManager();
	        int versionCode = 0;
	        try{
	            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
	            versionCode = packageInfo.versionCode;
	        }catch(NameNotFoundException e){
	        	Log.e(TAG,e.getMessage());
	        }
	        return versionCode;
		}
		
		@Override
		public TransferFolderResult transferFolder(ServerInfo source,
				String sourceLabel, ServerInfo target, String targetLabel,
				String successTrashLabel, boolean isDocomoConvertFlg,
				boolean isFilterTargetSeenFlg, String filterTargetDate,
				boolean isShowNotification)
				throws RemoteException {
			if(!mIsTaskRunning){
				mIsTaskRunning = true;				
				TransferFolderResult result =  transferMailProc(source, sourceLabel, target, targetLabel, successTrashLabel, isDocomoConvertFlg, isFilterTargetSeenFlg, filterTargetDate, isShowNotification);
				mIsTaskRunning = false;
				return result;
			}
			else{
				return null;
			}

		}
		
		@Override
		public boolean isTransferProcessActive() throws RemoteException {
			return mIsTaskRunning;
		}

		@Override
		public LabelResult getLabels(ServerInfo server) throws RemoteException {
			return ImapUtils.getLabels(server);
		}

		@Override
		public String login(ServerInfo server) throws RemoteException {
			return ImapUtils.login(server);
		}

		@Override
		public boolean taskCancel() throws RemoteException {
			mIsCanceld = true;
			return true;
		}	
		
		private TransferFolderResult transferMailProc(ServerInfo source,
				String sourceLabel, ServerInfo target, String targetLabel,
				String successTrashLabel, boolean isDocomoConvertFlg,
				final boolean isFilterTargetSeenFlg, final String filterTargetDate,
				boolean isShowNotification)
				throws RemoteException {
				int totalCount = 0;
				int successCount = 0;
				int errorCount = 0;
				String errorMessage = "";
				List<String> errorMessages =new ArrayList<String>();
				Properties props = System.getProperties();
				Session sessSource = Session.getInstance(props, null);
				Session sessTarget = Session.getInstance(props,null);
				Store storeSource = null;
				Store storeTarget = null;
				Folder folderSource = null;
				Folder folderTarget = null;
				Folder folderDelete = null;
				mIsCanceld = false;
					try {
						storeSource = sessSource.getStore(source.getProtocol());	//imapsStoreを取得
						storeTarget = sessTarget.getStore(target.getProtocol());
						storeSource.connect(source.getHostname(),source.getPort(),source.getId(),source.getPassword());
						storeTarget.connect(target.getHostname(),target.getPort(),target.getId(),target.getPassword());

						folderSource = storeSource.getFolder(sourceLabel);
						folderSource.open(Folder.READ_WRITE);
						if(!TextUtils.isEmpty(successTrashLabel)){
							folderDelete = storeSource.getFolder(successTrashLabel);
							folderDelete.open(Folder.READ_WRITE);				
						}
						folderTarget = storeTarget.getFolder(targetLabel);
						folderTarget.open(Folder.READ_WRITE);
						
						@SuppressWarnings("serial")
						Message[] messages = folderSource.search(new SearchTerm() {					
							@Override
							public boolean match(Message message) {
									if(!TextUtils.isEmpty(filterTargetDate)){
										try {
											Date targetDate = message.getReceivedDate();
											if(targetDate == null)
												targetDate = message.getSentDate();
											if(targetDate == null)
												return false;
											if(targetDate.compareTo(DateUtils.toCalendar(filterTargetDate, DATE_FORMAT).getTime()) > 0)
												return false;	//date after						
										} catch (ParseException e) {
												Log.e(TAG, e.getMessage());
												return false;
										}
										catch (MessagingException e) {
											Log.e(TAG, e.getMessage());
											return false;
										}
									}
									
									if(isFilterTargetSeenFlg){
										try {
											if(!message.getFlags().contains(Flag.SEEN))
												return false;
										}
										catch (MessagingException e) {
											Log.e(TAG, e.getMessage());
											return false;
										}
									}
									return true;
							}});
						totalCount = messages.length;
						for(Message m : messages){							
							String subject = m.getSubject();
							if(TextUtils.isEmpty(subject))
								subject = "no subject";
							//Notification Process
							if(isShowNotification)
								NotificationUtility.showNotification(getApplicationContext(), getString(R.string.mesNotification), String.format(getString(R.string.mesTitle),sourceLabel), subject);
							if(mIsCanceld){
								return new TransferFolderResult(TransferFolderResult.RESULT_CANCEL, totalCount, successCount, errorCount, errorMessage,null);
							}
							TransferMessageResult result = MailUtils.transfer(getApplicationContext(), sessTarget, folderTarget,new MimeMessage((MimeMessage)m), isDocomoConvertFlg);
							if(result.getResult_code() == TransferMessageResult.RESULT_SUCCESS){
								successCount++;
								if(!TextUtils.isEmpty(successTrashLabel)){
									m.getFolder().copyMessages(new Message[]{m}, folderDelete);
									m.setFlag(Flag.DELETED, true);
									m.getFolder().expunge();
								}
							}
							else{
								errorMessages.add("FROM:" + MailUtils.getFromAddress((MimeMessage)m) + 
										" / TO:" + MailUtils.getToAddress((MimeMessage)m) + 
										" / SUBJECT:" + m.getSubject() + " / ERROR:" + result.getMessage());
								errorCount++;
							}
						}				
					} catch (Exception e) {
						errorMessage = e.getMessage();
						return new TransferFolderResult(TransferFolderResult.RESULT_ERROR, totalCount, successCount, errorCount, errorMessage,null);
					} finally {
						if (folderDelete != null)
							try {
								if (folderDelete.isOpen())
									folderDelete.close(false);
							} catch (MessagingException e) {
								Log.e(TAG,e.getMessage());
							}

						if (folderSource != null)
							try {
								if (folderSource.isOpen())
									folderSource.close(false);
							} catch (MessagingException e) {
								Log.e(TAG,e.getMessage());
							}
						if (storeSource != null)
							try {
								storeSource.close();
							} catch (MessagingException e) {
								Log.e(TAG,e.getMessage());
							}
						
						if (folderTarget != null)
							try {
								if (folderTarget.isOpen())
									folderTarget.close(false);
							} catch (MessagingException e) {
								Log.e(TAG,e.getMessage());
							}
						if (storeTarget != null)
							try {
								storeTarget.close();
							} catch (MessagingException e) {
								Log.e(TAG,e.getMessage());
							}
						folderDelete = null;
						folderSource = null;
						storeSource = null;
						sessSource = null;
						folderTarget = null;
						storeTarget = null;
						sessTarget = null;
						if(mIsCanceld){
							return new TransferFolderResult(TransferFolderResult.RESULT_CANCEL, totalCount, successCount, errorCount, errorMessage,null);
						}
					}			
					mIsTaskRunning = false;
					return new TransferFolderResult(TransferFolderResult.RESULT_SUCCESS, totalCount, successCount, errorCount, errorMessage,errorMessages);
					
		}
	};
	
	
	

}
