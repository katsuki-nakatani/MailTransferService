package com.miruker.lib.mailtransferservice.Utils;

import java.io.IOException;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import android.content.Context;
import android.text.TextUtils;

import com.miruker.lib.mailtransferservice.TransferMessageResult;



public class MailUtils {
	

	/**
	 * 
	 * @param parts
	 * @param sourceCode
	 * @param convertCode
	 * @throws MessagingException
	 * @throws IOException
	 */
	public static void EncodeMultipartShiftJis(Multipart parts,String sourceCode , String convertCode) throws MessagingException, IOException{
		for(int i = 0;i<parts.getCount();i++){
			if(parts.getBodyPart(i).getContentType().contains("text/plain")){
				String data = (String)parts.getBodyPart(i).getContent();
				((MimeBodyPart)parts.getBodyPart(i)).setText(new String(data.getBytes(sourceCode),sourceCode),convertCode);
				parts.getBodyPart(i).setHeader("Content-Type", "text/plain; charset=" + convertCode);
			}
			else if(parts.getBodyPart(i).getContentType().contains("text/html")){
					String data = (String)parts.getBodyPart(i).getContent();
					((MimeBodyPart)parts.getBodyPart(i)).setText(new String(data.getBytes(sourceCode),sourceCode),convertCode);
					parts.getBodyPart(i).setHeader("Content-Type", "text/html; charset=" + convertCode);
			}
			else if(parts.getBodyPart(i).getContentType().contains("multipart")){
				EncodeMultipartShiftJis((Multipart)parts.getBodyPart(i).getContent(),sourceCode,convertCode);
			}
		}
	}

	/**
	 * キャリアメール判別
	 * @param message
	 * @param containsKey
	 * @return
	 */
	public static boolean containsMailCarrier(MimeMessage message , String containsKey){
		Address[] address;
		try {
			address = message.getFrom();
		} catch (MessagingException e) {
			return false;
		}
		if(address != null){
			for(Address single : address){
				if(((InternetAddress)single).getAddress().contains(containsKey)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 絵文字SJIS変換
	 * @param message
	 * @param sourceCode
	 * @param convertCode
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	public static MimeMessage convertEmoji(MimeMessage message,String sourceCode , String convertCode) throws MessagingException, IOException{

		if(message.getContentType().toLowerCase().contains("text/plain")){

			//ヘッダーのセット
			String subject = message.getSubject();
			if(!TextUtils.isEmpty(subject))
				message.setSubject(new String(subject.getBytes(sourceCode),sourceCode), convertCode);
			//Bodyのセット
			String part = (String)message.getContent();
			if(!TextUtils.isEmpty(part)){
				String text = new String(part.getBytes(sourceCode),sourceCode);
				message.setText(text, convertCode);
				message.setHeader("Content-Type", "text/plain; charset=" + convertCode);
			}
		}
		else if(message.getContentType().toLowerCase().contains(("multipart"))){
			//ヘッダーの変更
			String subject = message.getSubject();
			if(!TextUtils.isEmpty(subject))
				message.setSubject(new String(subject.getBytes(sourceCode),sourceCode), convertCode);
			//Bodyの変更
			MailUtils.EncodeMultipartShiftJis((Multipart)message.getContent(),sourceCode,convertCode);
		}

		return message;
	}

	private static MimeMessage convertEmojiHeader(Context con,MimeMessage message) throws MessagingException, IOException{
		MailUtils.convertEmoji(message,"UTF-8","Shift-JIS");

		String mailAddress = null;
		//Fromを取得
		Address[] address = message.getFrom();
		if(address != null){
			if(address.length > 0)
				mailAddress = ((InternetAddress)address[0]).getAddress();
		}
		message.setHeader("Envelope-From",mailAddress);
		//FROM句を取得して、
		String display = ContactUtils.getEmlAddressToDisplay(con, mailAddress);
		if(!TextUtils.isEmpty(display))
			display = display + "<" + mailAddress + ">";
		else
			display = mailAddress;
		message.setFrom(new InternetAddress("mailconvert@docomo.ne.jp",display,"UTF-8"));
		message.removeHeader("Received");
		message.saveChanges();
		return message;
	}
	
	public static String getFromAddress(MimeMessage message){
		//Fromを取得
		Address[] address;
		try {
			address = message.getFrom();
			if(address != null){
				if(address.length > 0)
					return ((InternetAddress)address[0]).getAddress();
			}
			return "no address";
		} catch (MessagingException e) {
			return "no address";
		}		
	}
	
	public static String getToAddress(MimeMessage message){
		StringBuilder builder = new StringBuilder();
		//Fromを取得
		Address[] address;
		try {
			address = message.getRecipients(RecipientType.TO);
			if(address != null){
				if(address.length > 0){
					for(Address a : address){
						builder.append(((InternetAddress)a).getAddress() + ",");
					}
					return builder.toString();
				}
			}
			return "no address";
		} catch (MessagingException e) {
			return "no address";
		}		
	}
	
	private static boolean isContentTypeCheck(String value){
		if(value.toLowerCase().contains("text/plain") || value.toLowerCase().contains("multipart"))
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param con
	 * @param sessTarget
	 * @param folderTarget
	 * @param message
	 * @param decoConvert
	 * @return
	 */
	public static TransferMessageResult transfer(Context con , Session sessTarget , Folder folderTarget,MimeMessage message,boolean decoConvert)
	{
		try
		{
			//絵文字
			if(decoConvert){
				//docomo
				if(MailUtils.containsMailCarrier(message, "@docomo.ne.jp")){
					if(isContentTypeCheck(message.getContentType())){
						message = MailUtils.convertEmoji(message,"UTF-8","Shift-JIS");
						message.saveChanges();
					}
				}
				//au
				if(MailUtils.containsMailCarrier(message, "@ezweb.ne.jp")){
					if(isContentTypeCheck(message.getContentType())){
						message = MailUtils.convertEmojiHeader(con, message);
					}
				}
				//softbank
				if(MailUtils.containsMailCarrier(message, "@softbank.ne.jp") ||
							MailUtils.containsMailCarrier(message, "@i.softbank.jp") ||
							MailUtils.containsMailCarrier(message, "vodafone.ne.jp")){
					if(isContentTypeCheck(message.getContentType())){
						message = MailUtils.convertEmojiHeader(con, message);
						}
				}
				//willcom
					if(MailUtils.containsMailCarrier(message, "@Willcom.com")||
							MailUtils.containsMailCarrier(message, "@wcm.ne.jp") ||
							MailUtils.containsMailCarrier(message, ".pdx.ne.jp") )
						if(isContentTypeCheck(message.getContentType())){
							message = MailUtils.convertEmojiHeader(con, message);
						}
				}

				folderTarget.appendMessages(new Message[] {message});				
				return new TransferMessageResult(TransferMessageResult.RESULT_SUCCESS, message.getMessageID());

			}
			catch(Exception e)
			{
				if(TextUtils.isEmpty(e.getMessage()))
						return new TransferMessageResult(TransferMessageResult.RESULT_ERROR, getFromAddress(message) +  "/unknown error");
				else
					return new TransferMessageResult(TransferMessageResult.RESULT_ERROR, getFromAddress(message) + "/" + e.getMessage());
			}
			finally
			{
				try{
					if(message != null)
						message.getInputStream().close();
					message = null;
				}
				catch(Exception e){
					
				}
			}
	}
}
