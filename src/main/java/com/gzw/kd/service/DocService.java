package com.gzw.kd.service;


import com.gzw.kd.common.entity.Doc;

import java.util.List;
@SuppressWarnings("all")
public interface DocService  {
    //根据名称和状态查询doc表
    List<Doc> getDocByNameAndStatus(String  name,int status)throws  Exception;

    //根据名称查询doc表
    List<Doc> getDocByName(String  name)throws  Exception;


    //根据id查询doc表
    Doc getDocById(Integer  id)throws  Exception;

    //起草文件，插入一条记录
    Integer addDoc(Doc doc)throws  Exception;

    //更新数据库doc表的status
    Integer updateStatusForDocById(Doc doc)throws  Exception;

    //更新数据库doc表的docName
    Integer updateDocNameForDocById(Doc doc)throws  Exception;

    //更新数据库doc表的pdfName
    Integer updatePdfNameForDocById(Doc doc)throws  Exception;

    //更新数据库doc表的result
    Integer updateResultForMsgId(String msgId,String result)throws  Exception;

    List<Doc> getAllDocs(Doc doc);
}
