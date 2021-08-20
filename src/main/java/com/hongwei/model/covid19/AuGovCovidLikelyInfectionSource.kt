package com.hongwei.model.covid19

enum class AuGovCovidLikelyInfectionSource(val display: String) {
    Overseas("Overseas"),
    Interstate("Interstate"),
    LocallyAccquiredNoLink("Locally acquired - no links to known case or cluster"),
    LocallyAccquiredLinked("Locally acquired - linked to known case or cluster"),
    LocallyAccquiredInvestigating("Locally acquired - investigation ongoing");

    companion object {
        fun parseFromString(string: String): AuGovCovidLikelyInfectionSource? = when (string) {
            Overseas.display -> Overseas
            Interstate.display -> Interstate
            LocallyAccquiredNoLink.display -> LocallyAccquiredNoLink
            LocallyAccquiredLinked.display -> LocallyAccquiredLinked
            LocallyAccquiredInvestigating.display -> LocallyAccquiredInvestigating
            else -> null
        }
    }
}