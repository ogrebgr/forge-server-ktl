package com.bolyartech.forge.server.module

import com.bolyartech.forge.server.HttpMethod
import com.bolyartech.forge.server.route.Route
import com.bolyartech.forge.server.route.RouteRegister
import java.text.MessageFormat
import javax.inject.Inject

class SiteModuleRegisterImpl @Inject constructor(private val routeRegister: RouteRegister) : SiteModuleRegister {

    private val modules: MutableList<SiteModule> = mutableListOf<SiteModule>()


    override fun registerModule(mod: SiteModule) {
        if (!isModuleRegistered(mod)) {
            modules.add(mod)
            for (ep in mod.createRoutes()) {
                if (!routeRegister.isRegistered(ep)) {
                    routeRegister.register((mod.getSystemName() + " (" + mod.getVersionName()).toString() + ")", ep)
                } else {
                    val reg = routeRegister.getRegistration(ep)!!
                    throw IllegalStateException(
                        MessageFormat.format(
                            "Path {0} already registered for module {1}",
                            reg.route.getPath(),
                            reg.moduleName
                        )
                    )
                }
            }
        } else {
            throw IllegalStateException(MessageFormat.format("Module '{0}' already registered", mod.getSystemName()))
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