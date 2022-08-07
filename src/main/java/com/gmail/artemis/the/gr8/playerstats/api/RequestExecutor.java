package com.gmail.artemis.the.gr8.playerstats.api;

import com.gmail.artemis.the.gr8.playerstats.statistic.StatRetriever;
import com.gmail.artemis.the.gr8.playerstats.statistic.result.StatResult;
import com.gmail.artemis.the.gr8.playerstats.statistic.request.StatRequest;

/** Completes a basic {@link StatRequest} provided by the {@link PlayerStatsAPI}
 and performs a statistic lookup with the information that is stored inside this StatRequest.*/
public interface RequestExecutor<T> {

  static StatRetriever getStatCalculator() {
    return PlayerStatsAPI.statCalculator();
  }

  static StatFormatter getStatFormatter() {
    return PlayerStatsAPI.statFormatter();
  }

  StatResult<T> execute();
}