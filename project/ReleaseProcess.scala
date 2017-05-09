import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport._
import ReleaseTransformations._

object ReleaseProcess {

    val settings = Seq(
        releaseIgnoreUntrackedFiles := true,
        releaseTagComment := s"Release ${(version in ThisBuild).value}",
        releaseCommitMessage := s"Set version to ${(version in ThisBuild).value}",
        releaseCrossBuild := false
    )

    val runPublishSigned = ReleaseStep(action = Command.process("publishSigned", _))

    val process =  Seq[ReleaseStep](
        checkSnapshotDependencies,
        inquireVersions,
        //runTest,
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        runPublishSigned,
        setNextVersion,
        commitNextVersion,
        pushChanges
    )

}
