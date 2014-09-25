package org.hxzon.admcmd;

import io.netty.channel.Channel;

import org.hxzon.util.Data;

public interface AdmClientManager {
    void addChannel(Channel channel, Data data);

    Data getChannelData(Channel channel);

    void removeChannel(Channel channel);

    void addAdmAccount(AdmAccount admAccount);

    void removeAdmAccount(String admName);

    AdmAccount findAdmAccount(String admName);
}
