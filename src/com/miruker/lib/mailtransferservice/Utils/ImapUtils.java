
package com.miruker.lib.mailtransferservice.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import com.miruker.lib.mailtransferservice.LabelResult;
import com.miruker.lib.mailtransferservice.ServerInfo;


public class ImapUtils {


	public static LabelResult getLabels(ServerInfo server)
	{
		List<String> resultLabels = new ArrayList<String>();
		Properties props = System.getProperties();
		Session sess = Session.getInstance(props, null);
		Store st = null;
		try
		{
			st = sess.getStore(server.getProtocol());
			st.connect(server.getHostname(),server.getPort(),server.getId(),server.getPassword());
			Folder[] folders = st.getDefaultFolder().list("*");

			for(Folder folder : folders)
			{
				resultLabels.add(folder.getFullName());
			}
			
			return new LabelResult(LabelResult.RESULT_SUCCESS,resultLabels,null);
		}
		catch(Exception ex)
		{
			return new LabelResult(LabelResult.RESULT_ERROR, null, ex.getClass().getName() + "/" + ex.getMessage());
		}
		finally{
			if(st != null)
				try {
					st.close();
				} catch (MessagingException e) {
				}
			st = null;
			if(sess != null)
				sess = null;
		}
	}


	public static String login(ServerInfo server)
	{

		Properties props = System.getProperties();
		Session sess = Session.getInstance(props, null);

		String returnMessage = null;
		Store st = null;
		try
		{
			st = sess.getStore(server.getProtocol());
			st.connect(server.getHostname(),server.getPort(),server.getId(),server.getPassword());
		}
		catch(Exception ex)
		{
			returnMessage = ex.getMessage();
		}
		finally{
			if(st != null)
				try {
					st.close();
				} catch (MessagingException e) {
				}
			st = null;
			if(sess != null)
				sess = null;
		}
		return returnMessage;
	}
	
}
