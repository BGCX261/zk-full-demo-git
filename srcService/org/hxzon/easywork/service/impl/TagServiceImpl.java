package org.hxzon.easywork.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hxzon.easywork.model.common.HTag;
import org.hxzon.easywork.service.TagService;

public class TagServiceImpl implements TagService {

    public List<HTag> listLike(String like, long first, long size) {
        List<HTag> tags = new ArrayList<HTag>();
        tags.add(new HTag("a"));
        tags.add(new HTag("b"));
        tags.add(new HTag("c"));
        return tags;
    }
}
