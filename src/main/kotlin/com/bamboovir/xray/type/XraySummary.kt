package com.bamboovir.xray.type

import com.jfrog.xray.client.services.summary.SummaryResponse
import kotlinx.serialization.Serializable

@Serializable
data class VulnerableComponents (
    val fixedVersions: List<String>,
)

@Serializable
data class Issue (
    val created: String?,
    val description: String?,
    val issueType: String?,
    val provider: String?,
    val severity: String?,
    val summary: String?,
    val impactPath: List<String>?,
    val vulnerableComponents: List<VulnerableComponents>,
)

@Serializable
data class License (
    val fullName: String?,
    val name: String?,
    val moreInfoURL: List<String>,
    val components: List<String>,
)

@Serializable
data class General (
        val componentID: String?,
        val name: String?,
        val path: String?,
        val pkgType: String?,
        val sha256: String?,
)

@Serializable
data class Artifact (
    val general: General?,
    val licenses: List<License>,
    val issues: List<Issue>,
)

@Serializable
data class XraySummary(
    val artifacts: List<Artifact>,
)

fun fromSummaryResponse(summaryResponse: SummaryResponse): XraySummary {
    val artifacts = mutableListOf<Artifact>()

    for (eArtifact in summaryResponse.artifacts) {
        val general = General(
                eArtifact.general.componentId,
                eArtifact.general.name,
                eArtifact.general.path,
                eArtifact.general.pkgType,
                eArtifact.general.sha256,
        )

        val licenses = mutableListOf<License>()

        for (eLicense in eArtifact.licenses) {
            val moreInfoURL = mutableListOf<String>()

            for (url in eLicense.moreInfoUrl()) {
                moreInfoURL.add(url)
            }

            val components = mutableListOf<String>()

            for (component in eLicense.components) {
                components.add(component)
            }

            val license = License(
                    eLicense.fullName,
                    eLicense.name,
                    moreInfoURL,
                    components
            )
            licenses.add(license)
        }

        val issues = mutableListOf<Issue>()

        for (eIssue in eArtifact.issues) {
            val impactPath = mutableListOf<String>()
            eIssue.impactPath?.forEach {
                impactPath.add(it)
            }

            val vulnerableComponents = mutableListOf<VulnerableComponents>()
            for (eVulnerableComponent in eIssue.vulnerableComponents) {
                val fixedVersions = mutableListOf<String>()
                for (fixedVersion in eVulnerableComponent.fixedVersions) {
                    fixedVersions.add(fixedVersion)
                }

                val vulnerableComponent = VulnerableComponents(fixedVersions)
                vulnerableComponents.add(vulnerableComponent)
            }

            val issue = Issue(
                    eIssue.created,
                    eIssue.description,
                    eIssue.issueType,
                    eIssue.provider,
                    eIssue.severity,
                    eIssue.summary,
                    impactPath,
                    vulnerableComponents,
            )

            issues.add(issue)
        }

        val artifact = Artifact(general, licenses, issues)

        artifacts.add(artifact)
    }
    return XraySummary(artifacts)
}