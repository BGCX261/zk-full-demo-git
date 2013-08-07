package org.hxzon.easywork.service;

import java.util.List;

import org.hxzon.easywork.model.common.HTag;

public interface TagService {

    List<HTag> listLike(String like, long first, long size);
}
