package site.goldenticket.common.constants;

public enum AreaCode {
    SEOUL(7, "서울"),
    GYEONGGI(8, "경기도"),
    INCHEON(9, "인천"),
    GANGWON(11, "강원"),
    JEJU(6601, "제주"),
    DAEJEON(10, "대전"),
    CHUNGBUK(50, "충북"),
    CHUNGNAM(13, "충남/세종"),
    BUSAN(14, "부산"),
    ULSAN(6602, "울산"),
    GYEONGNAM(6596, "경남"),
    DAEGU(6597, "대구"),
    GYEONGBUK(6598, "경북"),
    GWANGJU(100056, "광주"),
    JEONNAM(100064, "전남"),
    JEONBUK(6600, "전북");

    private final int areaCode;
    private final String areaName;

    AreaCode(int areaCode, String areaName) {
        this.areaCode = areaCode;
        this.areaName = areaName;
    }

    public int getAreaCode() {
        return areaCode;
    }

    public String getAreaName() {
        return areaName;
    }
}