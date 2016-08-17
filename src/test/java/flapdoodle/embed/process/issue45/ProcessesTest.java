package flapdoodle.embed.process.issue45;

import de.flapdoodle.embed.process.config.*;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.config.store.DownloadConfigBuilder;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.GenericVersion;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.extract.ImmutableExtractedFileSet;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.UUIDDir;
import de.flapdoodle.embed.process.io.progress.ConsoleOneLineProgressListener;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.runtime.Processes;
import de.flapdoodle.embed.process.store.ArtifactStoreBuilder;
import de.flapdoodle.embed.process.store.Downloader;
import de.flapdoodle.embed.process.store.IArtifactStore;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;

/**
 * @author [[mailto:michael@ahlers.consulting Michael Ahlers]]
 */
public class ProcessesTest {

    @Test
    public void verifyWindowsProcessId() throws Exception {
        final IVersion version = new GenericVersion("");
        final Distribution distribution = Distribution.detectFor(version);

        final IExecutableProcessConfig processConfig = new ExecutableProcessConfig(version, null);

        final IDownloadConfig downloadConfig =
                new DownloadConfigBuilder()
                        .packageResolver(new MockPackageResolver())
                        .downloadPath("")
                        .downloadPrefix("")
                        .artifactStorePath(new UUIDDir())
                        .fileNaming(new UUIDTempNaming())
                        .progressListener(new ConsoleOneLineProgressListener())
                        .userAgent("")
                        .build();

        final IArtifactStore artifactStore =
                new ArtifactStoreBuilder()
                        .download(downloadConfig)
                        .downloader(new Downloader())
                        .tempDir(new UUIDDir())
                        .executableNaming(new UUIDTempNaming())
                        .build();

        final IRuntimeConfig runtimeConfig =
                new RuntimeConfigBuilder()
                        .artifactStore(artifactStore)
                        .processOutput(ProcessOutput.getDefaultInstanceSilent())
                        .commandLinePostProcessor(new ICommandLinePostProcessor.Noop())
                        .build();

        final IExtractedFileSet files =
                new ImmutableExtractedFileSet.Builder()
                        .baseDir(new File(""))
                        .executable(new File(""))
                        .build();

        final MockExecutable executable = new MockExecutable(distribution, processConfig, runtimeConfig, files);
        final MockProcess process = executable.start();

        final Method windowsProcessId = Processes.class.getDeclaredMethod("windowsProcessId", Process.class);
        windowsProcessId.setAccessible(true);
        windowsProcessId.invoke(null, process);
    }

}