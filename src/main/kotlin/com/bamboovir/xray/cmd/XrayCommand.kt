package com.bamboovir.xray.cmd

import com.bamboovir.xray.type.fromSummaryResponse
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.jfrog.xray.client.impl.ComponentsFactory
import com.jfrog.xray.client.impl.XrayClientBuilder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class XrayCommand: CliktCommand() {
    override fun run() = Unit
}

class ScanCommand: CliktCommand(help = "Scan By XrayID") {
    private val id by argument(name = "id", help = "Xray ID")
    private val xrayURL by option(help = "Xray Host").required()
    private val xrayUsername by option(help = "Username").required()
    private val xrayPassword by option(help = "Password").required()

    override fun run() {
        val xrayClient = (
                XrayClientBuilder()
                    .setUrl(xrayURL)
                    .setUserName(xrayUsername)
                    .setPassword(xrayPassword) as XrayClientBuilder
                ).build()

        val components = ComponentsFactory.create()
        components.addComponent(id, "")
        val summaryResponse = xrayClient.summary().component(components)
        val xraySummary = fromSummaryResponse(summaryResponse)
        val xrayScanResult = Json.encodeToString(xraySummary)
        echo(xrayScanResult)
    }
}

fun newXrayCommandCLI(args: Array<String>) {
    return XrayCommand().subcommands(ScanCommand()).main(args)
}