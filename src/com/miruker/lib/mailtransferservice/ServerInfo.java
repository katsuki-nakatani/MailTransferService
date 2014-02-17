package com.miruker.lib.mailtransferservice;

import lombok.Data;
import android.os.Parcel;
import android.os.Parcelable;

public @Data class ServerInfo implements Parcelable {
	
	public static final String PROTOCOL_IMAP = "imaps";
	public static final String PROTOCOL_IMAPS = "imaps";

	
	private String protocol = PROTOCOL_IMAP;
	private String hostname;
	private int port;
	private String id;
	private String password;
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(protocol);
		dest.writeString(hostname);	
		dest.writeInt(port);
		dest.writeString(id);	
		dest.writeString(password);	
	}
	
	public static final Parcelable.Creator<ServerInfo> CREATOR = new Creator<ServerInfo>() {
		
		@Override
		public ServerInfo[] newArray(int size) {
			return new ServerInfo[size];
		}
		
		@Override
		public ServerInfo createFromParcel(Parcel source) {
			return new ServerInfo(source);
		}
	};
	
	private ServerInfo(Parcel parcel){
		protocol = parcel.readString();
		hostname = parcel.readString();
		port = parcel.readInt();
		id = parcel.readString();
		password = parcel.readString();
	}
	public ServerInfo(String protocol, String hostname, int port, String id,
			String password) {
		super();
		this.protocol = protocol;
		this.hostname = hostname;
		this.port = port;
		this.id = id;
		this.password = password;
	}
}
