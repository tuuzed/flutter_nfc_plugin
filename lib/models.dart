class ReadTagArg {
  ReadTagArg({
    this.sector,
    this.block,
    this.keyType = KeyType.keyA,
    this.hexKey = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
  });

  int sector;
  int block;
  KeyType keyType;
  String hexKey;

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
      "hexKey": hexKey,
    };
  }
}

class WriteTagArg {
  WriteTagArg({
    this.sector,
    this.block,
    this.hexData = "00000000000000000000000000000000",
    this.keyType = KeyType.keyA,
    this.hexKey = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF",
  });

  int sector;
  int block;
  String hexData;
  KeyType keyType;
  String hexKey;

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
      "hexData": hexData,
      "keyType": kt,
      "hexKey": hexKey,
    };
  }
}

class TagResult {
  TagResult(
      {this.type, this.success, this.hexId, this.techList, this.dataList});

  TagResultType type;
  bool success;
  String hexId;
  List<String> techList;
  List<TagResultData> dataList;

  factory TagResult.formMap(map) {
    var rst = TagResult(
      success: map["success"],
      hexId: map["hexId"],
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
  TagResultData({this.sector, this.block, this.hexData});

  int sector;
  int block;
  String hexData;

  factory TagResultData.formMap(map) {
    return TagResultData(
      sector: map["sector"],
      block: map["block"],
      hexData: map["hexData"],
    );
  }
}

enum TagResultType { none, foundTag, readTag, writeTag }
enum KeyType { keyA, keyB }
