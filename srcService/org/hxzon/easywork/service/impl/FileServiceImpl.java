package org.hxzon.easywork.service.impl;

import javax.annotation.Resource;

import org.hxzon.easywork.model.common.HFile;
import org.hxzon.easywork.service.FileService;
import org.hxzon.project.Dao;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

    @Resource
    private Dao dao;

    public void listByTitle(String title, long first, long size) {
        String hql = " where p.title like :title order by title";
        dao.list(HFile.class, hql, first, size,//
                "title", ("%" + title + "%"));
    }

    public void add(HFile file) {
        dao.add(file);
    }

}
