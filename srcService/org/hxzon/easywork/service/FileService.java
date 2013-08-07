package org.hxzon.easywork.service;

import org.hxzon.easywork.model.common.HFile;

public interface FileService {

    void listByTitle(String title,long first,long size);
    
    void add(HFile file);
}
