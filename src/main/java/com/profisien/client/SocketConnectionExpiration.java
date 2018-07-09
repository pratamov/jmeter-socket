package com.profisien.client;

import stormpot.Expiration;
import stormpot.SlotInfo;

public class SocketConnectionExpiration implements Expiration<SocketConnectionDao>{

	@Override
	public boolean hasExpired(SlotInfo<? extends SocketConnectionDao> info) throws Exception {
		return info.getPoolable().isClosed();
	}

}