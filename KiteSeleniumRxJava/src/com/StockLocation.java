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
    map.put("AJANTPHARM", 53);
    map.put("ADANIENT", 54);
    map.put("ADANIPOWER", 55);
    map.put("AJANTPHARM", 56);
    map.put("ADANIENT", 57);
    map.put("ADANIPOWER", 58);
    map.put("AJANTPHARM", 59);
    map.put("ADANIENT", 60);
    map.put("ADANIPOWER", 61);
    map.put("AJANTPHARM", 62);
    map.put("ADANIENT", 63);
    map.put("ADANIPOWER", 64);
    map.put("AJANTPHARM", 65);
    map.put("ADANIENT", 66);
    map.put("ADANIPOWER", 67);
    map.put("AJANTPHARM", 68);
    map.put("ADANIENT", 69);
    map.put("ADANIPOWER", 70);
    map.put("AJANTPHARM", 71);
    map.put("ADANIENT", 72);
    map.put("ADANIPOWER", 73);
    map.put("AJANTPHARM", 74);
    map.put("ADANIENT", 75);
    map.put("ADANIPOWER", 76);
    map.put("AJANTPHARM", 77);
    map.put("ADANIENT", 78);
    map.put("ADANIPOWER", 79);
    map.put("AJANTPHARM", 80);
    map.put("ADANIENT", 81);
    map.put("ADANIPOWER", 82);
    map.put("AJANTPHARM", 83);
    map.put("ADANIENT", 84);
    map.put("ADANIPOWER", 85);
    map.put("AJANTPHARM", 86);
    map.put("ADANIENT", 87);
    map.put("ADANIPOWER", 88);
    map.put("AJANTPHARM", 89);
    map.put("ADANIENT", 90);
    map.put("ADANIPOWER", 91);
    map.put("AJANTPHARM", 92);
    map.put("ADANIENT", 93);
    map.put("ADANIPOWER", 94);
    map.put("AJANTPHARM", 95);
    map.put("ADANIENT", 96);
    map.put("ADANIPOWER", 97);
    map.put("AJANTPHARM", 98);
    map.put("ADANIENT", 99);
    map.put("ADANIPOWER", 100);
  }

  public static String getPosition(String name) {
    return (map.get(name) % 20) + "";
  }

  public static String getMarketWatch(String name) {
    return (map.get(name) / 20) + "";
  }
}
