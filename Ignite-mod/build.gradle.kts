plugins {
  id("mod.base-conventions")
}

dependencies {
  remapper("net.fabricmc:tiny-remapper:0.10.4:fat")

  compileOnly(libs.ignite)
  compileOnly(libs.mixin)
  compileOnly(libs.mixinExtras)

  paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
}
