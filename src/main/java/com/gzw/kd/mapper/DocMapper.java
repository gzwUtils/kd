package com.gzw.kd.mapper;


import com.gzw.kd.common.entity.Doc;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@SuppressWarnings("all")
@Repository
@Mapper
public interface DocMapper {
    List<Doc> getDocByNameAndStatus(String name, int status) throws Exception;

    List<Doc> getDocByName(String name) throws Exception;

    Integer addDoc(Doc doc) throws Exception;

    //根据id查询doc表
    Doc getDocById(Integer  id)throws  Exception;

    //更新数据库doc表的status
    Integer updateStatusForDocById(Doc doc) throws Exception;

    //更新数据库doc表的docName
    Integer updateDocNameForDocById(Doc doc) throws Exception;

    //更新数据库doc表的pdfName
    Integer updatePdfNameForDocById(Doc doc) throws Exception;

    List<Doc> getAllDocs(Doc doc);
    //更新数据库doc表的result
    Integer updateResultForMsgId(String msgId,String result)throws  Exception;

}
