part of 'flutter_nfc_plugin.dart';

enum TagResultType { none, foundTag, readTag, writeTag }
enum KeyType { keyA, keyB }

class ReadTagArg {
  ReadTagArg({
    required this.sector,
    required this.block,
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
    required this.sector,
    required this.block,
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
  TagResult({
    required this.type,
    required this.success,
    required this.hexId,
    required this.techList,
    required this.dataList,
  });

  TagResultType type;
  bool success;
  String hexId;
  List<String> techList;
  List<TagResultData> dataList;

  factory TagResult.formMap(map) {
    late final TagResultType type;
    late final List<TagResultData> dataList;
    switch (map["type"]) {
      case "foundTag":
        type = TagResultType.foundTag;
        break;
      case "readTag":
        type = TagResultType.readTag;
        break;
      case "writeTag":
        type = TagResultType.writeTag;
        break;
      default:
        type = TagResultType.none;
        break;
    }
    if (map["dataList"] is List) {
      dataList =
          map["dataList"].map((it) => TagResultData.formMap(it)).toList();
    } else {
      dataList = [];
    }
    return TagResult(
      type: type,
      success: map["success"],
      hexId: map["hexId"],
      techList: map["techList"].split(","),
      dataList: dataList,
    );
  }
}

class TagResultData {
  TagResultData({
    required this.sector,
    required this.block,
    required this.hexData,
  });

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
