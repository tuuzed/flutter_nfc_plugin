class ReadTagArg {
  ReadTagArg({
    this.sector,
    this.block,
    this.keyType = KeyType.keyA,
    this.key = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
  });

  int sector;
  int block;
  KeyType keyType;
  String key;

  Map<String, dynamic> toMap() {
    var kt = "keyA";
    switch (keyType) {
      case KeyType.keyA:
        kt = "keyA";
        break;
      case KeyType.keyB:
        kt = "keyB";
        break;
    }
    return {
      "sector": sector,
      "block": block,
      "keyType": kt,
      "key": key,
    };
  }
}

class WriteTagArg {
  WriteTagArg({
    this.sector,
    this.block,
    this.data = "00000000000000000000000000000000",
    this.keyType = KeyType.keyA,
    this.key = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
  });

  int sector;
  int block;
  String data;
  KeyType keyType;
  String key;

  Map<String, dynamic> toMap() {
    var kt = "keyA";
    switch (keyType) {
      case KeyType.keyA:
        kt = "keyA";
        break;
      case KeyType.keyB:
        kt = "keyB";
        break;
    }
    return {
      "sector": sector,
      "block": block,
      "data": data,
      "keyType": kt,
      "key": key,
    };
  }
}

class TagResult {
  TagResult({this.type, this.success, this.id, this.techList, this.dataList});

  TagResultType type;
  bool success;
  String id;
  List<String> techList;
  List<TagResultData> dataList;

  factory TagResult.formMap(map) {
    var rst = TagResult(
      success: map["success"],
      id: map["id"],
      techList: map["techList"].split(","),
    );
    switch (map["type"]) {
      case "foundTag":
        rst.type = TagResultType.foundTag;
        break;
      case "readTag":
        rst.type = TagResultType.readTag;
        break;
      case "writeTag":
        rst.type = TagResultType.writeTag;
        break;
      default:
        rst.type = TagResultType.none;
        break;
    }
    var dataList = map["dataList"];
    if (dataList != null && dataList is List) {
      rst.dataList = dataList.map((it) => TagResultData.formMap(it)).toList();
    }
    return rst;
  }
}

class TagResultData {
  TagResultData({this.sector, this.block, this.data});

  int sector;
  int block;
  String data;

  factory TagResultData.formMap(map) {
    return TagResultData(
      sector: map["sector"],
      block: map["block"],
      data: map["data"],
    );
  }
}

enum TagResultType { none, foundTag, readTag, writeTag }
enum KeyType { keyA, keyB }
