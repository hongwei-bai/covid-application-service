package com.hongwei.model.v1.covid19.nsw

import com.hongwei.model.common.AuState


object PostcodeToStateMap {
    fun toState(postcode: Long?): AuState? =
            postcode?.let {
                when (postcode) {
                    in 1000..1999 -> AuState.Nsw
                    in 2000..2599 -> AuState.Nsw
                    in 2619..2899 -> AuState.Nsw
                    in 2921..2999 -> AuState.Nsw

                    in 200..299 -> AuState.Act
                    in 2600..2618 -> AuState.Act
                    in 2900..2920 -> AuState.Act

                    in 3000..3999 -> AuState.Vic
                    in 8000..8999 -> AuState.Vic

                    in 4000..4999 -> AuState.Qld
                    in 9000..9999 -> AuState.Qld

                    in 5000..5799 -> AuState.Sa
                    in 5800..5999 -> AuState.Sa

                    in 6000..6797 -> AuState.Wa
                    in 6800..6999 -> AuState.Wa

                    in 7000..7799 -> AuState.Tas
                    in 7800..7999 -> AuState.Tas

                    in 800..899 -> AuState.Nt
                    in 900..999 -> AuState.Nt

                    else -> null
                }
            }
}