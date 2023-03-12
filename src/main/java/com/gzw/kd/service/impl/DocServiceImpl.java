package com.gzw.kd.service.impl;


import com.gzw.kd.common.entity.Doc;
import com.gzw.kd.mapper.DocMapper;
import com.gzw.kd.service.DocService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author 高志伟
 */
@Service
public class DocServiceImpl implements DocService {

    @Resource
    DocMapper docMapper;

    @Override
    public List<Doc> getDocByNameAndStatus(String name, int status) throws Exception {
        return docMapper.getDocByNameAndStatus(name,status);
    }

    @Override
    public List<Doc> getDocByName(String name) throws Exception {
        return docMapper.getDocByName(name);
    }

    @Override
    public Doc getDocById(Integer id) throws Exception {
        return docMapper.getDocById(id);
    }

    @Override
    public Integer addDoc(Doc doc) throws Exception {
        return docMapper.addDoc(doc);
    }

    @Override
    public Integer updateStatusForDocById(Doc doc) throws Exception {
        return docMapper.updateStatusForDocById(doc);
    }

    @Override
    public Integer updateDocNameForDocById(Doc doc) throws Exception {
        return docMapper.updateDocNameForDocById(doc);
    }

    @Override
    public Integer updatePdfNameForDocById(Doc doc) throws Exception {
        return docMapper.updatePdfNameForDocById(doc);
    }

    @Override
    public Integer updateResultForMsgId(String msgId,String result) throws Exception {
        return docMapper.updateResultForMsgId(msgId,result);
    }

    @Override
    public List<Doc> getAllDocs(Doc doc) {
        return docMapper.getAllDocs(doc);
    }
}
