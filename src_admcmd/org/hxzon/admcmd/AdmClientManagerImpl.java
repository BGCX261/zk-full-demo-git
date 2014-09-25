package org.hxzon.admcmd;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hxzon.util.Data;

public class AdmClientManagerImpl implements AdmClientManager {

    private Map<Channel, Data> channelDataMap;
    private Map<String, AdmAccount> admins;

    public AdmClientManagerImpl() {
        channelDataMap = new ConcurrentHashMap<Channel, Data>();
        admins = new HashMap<String, AdmAccount>();
    }

    @Override
    public void addChannel(Channel channel, Data data) {
        channelDataMap.put(channel, data);
    }

    @Override
    public Data getChannelData(Channel channel) {
        return channelDataMap.get(channel);
    }

    @Override
    public void removeChannel(Channel channel) {
        channelDataMap.remove(channel);
    }

    //===============
    @Override
    public void addAdmAccount(AdmAccount admAccount) {
        admins.put(admAccount.getName(), admAccount);
    }

    @Override
    public void removeAdmAccount(String admName) {
        admins.remove(admName);
    }

    @Override
    public AdmAccount findAdmAccount(String admName) {
        return admins.get(admName);
    }
}
