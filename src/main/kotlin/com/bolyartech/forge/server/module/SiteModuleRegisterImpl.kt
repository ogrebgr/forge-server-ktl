package com.bolyartech.forge.server.module

import com.bolyartech.forge.server.HttpMethod
import com.bolyartech.forge.server.route.Route
import com.bolyartech.forge.server.route.RouteRegister
import javax.inject.Inject

class SiteModuleRegisterImpl @Inject constructor(private val routeRegister: RouteRegister) : SiteModuleRegister {

    private val modules: MutableList<SiteModule> = mutableListOf<SiteModule>()


    override fun registerModule(mod: SiteModule) {
        if (!isModuleRegistered(mod)) {
            modules.add(mod)
            for (route in mod.createRoutes()) {
                routeRegister.register((mod.getSystemName() + " (" + mod.getVersionName()).toString() + ")", route)
            }
        } else {
            throw IllegalStateException("Module '${mod.getSystemName()}' already registered")
        }
    }


    override fun isModuleRegistered(mod: SiteModule): Boolean {
        var ret = false
        for (m in modules) {
            if (m.getSystemName().lowercase() == mod.getSystemName().lowercase()) {
                ret = true
                break
            }
        }
        return ret
    }

    override fun match(method: HttpMethod, path: String): Route? {
        return routeRegister.match(method, path)
    }

}