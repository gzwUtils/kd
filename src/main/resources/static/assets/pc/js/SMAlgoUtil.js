function SMUtils(){

}

SMUtils.SM3 = function (plainText){
  var dataBy = Hex.utf8StrToBytes(plainText);
  var sm3 = new SM3Digest();
  sm3.update(dataBy,0,dataBy.length);//数据很多的话，可以分多次update
  var sm3Hash = sm3.doFinal();//得到的数据是个byte数组
  return Hex.encode(sm3Hash,0,sm3Hash.length);//编码成16进制可见字符
}

SMUtils.SM4EncryptCBC = function (plainText,key,iv){
  var dataBy = Hex.utf8StrToBytes(plainText);
  var sm4 = new SM4();
  var key1 = Hex.decode(key);
  var iv1 = Hex.decode(iv);
  var timestamp = sm4.encrypt_cbc(key1, iv1, dataBy);
  return Hex.encode(timestamp,0,timestamp.length);
}