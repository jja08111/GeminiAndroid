import com.android.build.gradle.LibraryExtension
import io.github.jja08111.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply("com.android.library")
      val extension = extensions.getByType<LibraryExtension>()
      configureAndroidCompose(extension)
    }
  }
}
