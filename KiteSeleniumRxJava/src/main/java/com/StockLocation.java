package com;

import java.util.HashMap;
import java.util.Map;

public class StockLocation {
  private static final Map<String, Integer> map = new HashMap<>();

  static {
    map.put("ABB", 1);
    map.put("ACC", 2);
    map.put("ASHOKLEY", 3);
    map.put("DMART", 4);
    map.put("BAJAJFINSV", 5);
    map.put("BANKBARODA", 6);
    map.put("BEL", 7);
    map.put("BHEL", 8);
    map.put("BRITANNIA", 9);
    map.put("CADILAHC", 10);
    map.put("COLPAL", 11);
    map.put("CONCOR", 12);
    map.put("CUMMINSIND", 13);
    map.put("DLF", 14);
    map.put("DABUR", 15);
    map.put("EMAMILTD", 16);
    map.put("GSKCONS", 17);
    map.put("GLAXO", 18);
    map.put("GLENMARK", 19);
    map.put("GODREJCP", 20);
    map.put("HAVELLS", 21);
    map.put("HINDZINC", 22);
    map.put("ICICIPRULI", 23);
    map.put("IDEA", 24);
    map.put("INDIGO", 25);
    map.put("JSWSTEEL", 26);
    map.put("LICHSGFIN", 27);
    map.put("MRF", 28);
    map.put("MARICO", 29);
    map.put("MOTHERSUMI", 30);
    map.put("NHPC", 31);
    map.put("NMDC", 32);
    map.put("OIL", 33);
    map.put("OFSS", 34);
    map.put("PETRONET", 35);
    map.put("PIDILITIND", 36);
    map.put("PEL", 37);
    map.put("PFC", 38);
    map.put("PGHH", 39);
    map.put("PNB", 40);
    map.put("RECLTD", 41);
    map.put("SHREECEM", 42);
    map.put("SRTRANSFIN", 43);
    map.put("SIEMENS", 44);
    map.put("SAIL", 45);
    map.put("SUNTV", 46);
    map.put("TATAPOWER", 47);
    map.put("TITAN", 48);
    map.put("TORNTPHARM", 49);
    map.put("MCDOWELL-N", 50);
    map.put("ADANIENT", 51);
    map.put("ADANIPOWER", 52);
    map.put("AMARAJABAT", 53);
    map.put("AJANTPHARM", 54);
    map.put("APOLLOHOSP", 55);
    map.put("APOLLOTYRE", 56);
    map.put("ARVIND", 57);
    map.put("BANKINDIA", 58);
    map.put("BERGEPAINT", 59);
    map.put("BHARATFORG", 60);
    map.put("BIOCON", 61);
    map.put("CESC", 62);
    map.put("CANBK", 63);
    map.put("CASTROLIND", 64);
    map.put("CENTURYTEX", 65);
    map.put("DALMIABHA", 66);
    map.put("DISHTV", 67);
    map.put("DIVISLAB", 68);
    map.put("ENGINERSIN", 69);
    map.put("EXIDEIND", 70);
    map.put("FEDERALBNK", 71);
    map.put("GMRINFRA", 72);
    map.put("GODREJIND", 73);
    map.put("IDBI", 74);
    map.put("IDFCBANK", 75);
    map.put("IDFC", 76);
    map.put("IRB", 77);
    map.put("IGL", 78);
    map.put("JSWENERGY", 79);
    map.put("JINDALSTEL", 80);
    map.put("L&TFIN", 81);
    map.put("M&MFIN", 82);
    map.put("MRPL", 83);
    map.put("MINDTREE", 84);
    map.put("MUTHOOTFIN", 85);
    map.put("PCJEWELLER", 86);
    map.put("PAGEIND", 87);
    map.put("RBLBANK", 88);
    map.put("RCOM", 89);
    map.put("RELINFRA", 90);
    map.put("RPOWER", 91);
    map.put("SRF", 92);
    map.put("STAR", 93);
    map.put("TVSMOTOR", 94);
    map.put("TATACHEM", 95);
    map.put("TATACOMM", 96);
    map.put("TATAGLOBAL", 97);
    map.put("UNIONBANK", 98);
    map.put("UBL", 99);
    map.put("VOLTAS", 100);
  }

  public static String getMarketWatch(String name) {
    return ((map.get(name) / 20) + 2) + "";
  }

  public static String getPosition(String name) {
    return ((map.get(name) % 20) == 0 ? 20 : +(map.get(name) % 20)) + "";
  }
}
