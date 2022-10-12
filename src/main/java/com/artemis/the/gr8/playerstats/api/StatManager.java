package com.artemis.the.gr8.playerstats.api;

import com.artemis.the.gr8.playerstats.statistic.StatRequest;

import java.util.LinkedHashMap;

/**
 * Turns user input into a {@link StatRequest} that can be
 * used to get statistic data.
 */
public interface StatManager {

    /** Gets a RequestGenerator that can be used to create a PlayerStatRequest.
     * This RequestGenerator will make sure all default settings
     * for a player-statistic-lookup are configured.
     *
     * @param playerName the player whose statistic is being requested
     * @return the RequestGenerator */
    RequestGenerator<Integer> playerStatRequest(String playerName);

    /** Gets a RequestGenerator that can be used to create a ServerStatRequest.
     * This RequestGenerator will make sure all default settings
     * for a server-statistic-lookup are configured.
     *
     * @return the RequestGenerator*/
    RequestGenerator<Long> serverStatRequest();

    /** Gets a RequestGenerator that can be used to create a TopStatRequest
     * for a top-list of the specified size. This RequestGenerator will
     * make sure all default settings for a top-statistic-lookup are configured.
     *
     * @param topListSize how big the top-x should be (10 by default)
     * @return the RequestGenerator*/
    RequestGenerator<LinkedHashMap<String, Integer>> topStatRequest(int topListSize);

    /** Gets a RequestGenerator that can be used to create a TopStatRequest
     * for all offline players on the server (those that are included by
     * PlayerStats' settings). This RequestGenerator will make sure
     * all default settings for a top-statistic-lookup are configured.
     *
     * @return the RequestGenerator*/
    RequestGenerator<LinkedHashMap<String, Integer>> totalTopStatRequest();
}