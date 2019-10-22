package dev.rupansh.kernelsu

import com.topjohnwu.superuser.Shell

object SuOps {

    var uidList = getApps()

    fun addUid(uid: Int) {
        Shell.su("echo $uid > /sys/module/superuser/parameters/add_uid").exec()
    }

    fun delUid(uid: Int) {
        Shell.su("echo $uid > /sys/module/superuser/parameters/del_uid").exec()
    }

    fun getApps(): MutableList<Int>{
        val result: MutableList<Int> = mutableListOf()
        Shell.su("echo 420420420 > /sys/module/superuser/parameters/del_uid").exec()
        var shellOut = Shell.su("cat /sys/module/superuser/parameters/add_uid").exec().out
        val numUid = shellOut[0].split(" ")[1].toInt()
        result += shellOut[0].split(" ")[0].toInt()
        for (i in 2..numUid){
            shellOut = Shell.su("cat /sys/module/superuser/parameters/add_uid").exec().out
            result += shellOut[0].split(" ")[0].toInt()
        }

        return result
    }
}