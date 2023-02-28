package com.artemis.the.gr8.playerstats.api;

import java.util.LinkedHashMap;

public interface StatManager {

    /** Checks if the player belonging to this name
     * is on PlayerStats' exclude-list (meaning this
     * player is not counted for the server total, and
     * does not show in top results).
     *
     * @param playerName the name of the player to check
     * @return true if this player is on the exclude-list
     */
    boolean isExcludedPlayer(String playerName);

    /** Gets a RequestGenerator that can be used to create a PlayerStatRequest.
     * This RequestGenerator will make sure all default settings
     * for a player-statistic-lookup are configured.
     *
     * @param playerName the player whose statistic is being requested
     * @return the RequestGenerator */
    RequestGenerator<Integer> createPlayerStatRequest(String playerName);

    /**
     * Executes this StatRequest. This calculation can take some time,
     * so don't call this from the main Thread if you can help it!
     *
     * @return a StatResult containing the value of this lookup, both as
     * numerical value and as formatted message
     * @see PlayerStats
     * @see StatResult
     */
    StatResult<Integer> executePlayerStatRequest(StatRequest<Integer> request);

    /** Gets a RequestGenerator that can be used to create a ServerStatRequest.
     * This RequestGenerator will make sure all default settings
     * for a server-statistic-lookup are configured.
     *
     * @return the RequestGenerator*/
    RequestGenerator<Long> createServerStatRequest();

    /**
     * Executes this StatRequest. This calculation can take some time,
     * so don't call this from the main Thread if you can help it!
     *
     * @return a StatResult containing the value of this lookup, both as
     * numerical value and as formatted message
     * @see PlayerStats
     * @see StatResult
     */
    StatResult<Long> executeServerStatRequest(StatRequest<Long> request);

    /** Gets a RequestGenerator that can be used to create a TopStatRequest
     * for a top-list of the specified size. This RequestGenerator will
     * make sure all default settings for a top-statistic-lookup are configured.
     *
     * @param topListSize how big the top-x should be (10 by default)
     * @return the RequestGenerator*/
    RequestGenerator<LinkedHashMap<String, Integer>> createTopStatRequest(int topListSize);

    /** Gets a RequestGenerator that can be used to create a TopStatRequest
     * for all offline players on the server (those that are included by
     * PlayerStats' settings). This RequestGenerator will make sure
     * all default settings for a top-statistic-lookup are configured.
     *
     * @return the RequestGenerator*/
    RequestGenerator<LinkedHashMap<String, Integer>> createTotalTopStatRequest();

    /**
     * Executes this StatRequest. This calculation can take some time,
     * so don't call this from the main Thread if you can help it!
     *
     * @return a StatResult containing the value of this lookup, both as
     * numerical value and as formatted message
     * @see PlayerStats
     * @see StatResult
     */
    StatResult<LinkedHashMap<String, Integer>> executeTopRequest(StatRequest<LinkedHashMap<String, Integer>> request);
}