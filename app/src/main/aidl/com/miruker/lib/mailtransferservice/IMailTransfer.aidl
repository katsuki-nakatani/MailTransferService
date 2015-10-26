package com.miruker.lib.mailtransferservice;

import com.miruker.lib.mailtransferservice.LabelResult;
import com.miruker.lib.mailtransferservice.TransferFolderResult;
import com.miruker.lib.mailtransferservice.ServerInfo;

interface IMailTransfer{
	int getAppVersion();
	TransferFolderResult transferFolder(in ServerInfo source,
			in String sourceLabel,
			in ServerInfo target,
			in String targetLabel,
			in String successTrashLabel,
			in boolean isDocomoConvertFlg,
			in boolean isFilterTargetSeenFlg,
			in String filterTargetDate,
			in boolean isShowNotification);
			
	boolean isTransferProcessActive();
	LabelResult getLabels(in ServerInfo server);
	String login(in ServerInfo server);
	
	boolean taskCancel();
}