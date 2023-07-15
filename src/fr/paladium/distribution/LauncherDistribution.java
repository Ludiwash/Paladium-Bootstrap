package fr.paladium.distribution;

import java.util.LinkedHashMap;

public class LauncherDistribution {
   public String version;
   public LinkedHashMap<DistributionOS, String> java;
   public LinkedHashMap<DistributionOS, DistributionFile> launcher;
}
