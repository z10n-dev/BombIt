{
  description = "BombIt Java and Processing development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = { nixpkgs, ... }:
    let
      supportedSystems = [
        "x86_64-linux"
        "aarch64-linux"
      ];

      forAllSystems = nixpkgs.lib.genAttrs supportedSystems;
    in
    {
      devShells = forAllSystems (
        system:
        let
          pkgs = import nixpkgs { inherit system; };

          graphicsLibraries = with pkgs; [
            libGL
            libGLU
            mesa
            libxkbcommon
            wayland
            xorg.libX11
            xorg.libXcursor
            xorg.libXext
            xorg.libXi
            xorg.libXinerama
            xorg.libXrandr
            xorg.libXrender
            xorg.libXtst
            xorg.libXxf86vm
            libxcb
          ];
        in
        {
          default = pkgs.mkShell {
            packages = with pkgs; [
              jdk21
              maven
              fontconfig
              freetype
              alsa-lib
              jdt-language-server
              vscode-extensions.vscjava.vscode-java-debug
              google-java-format
            ] ++ graphicsLibraries;

            JAVA_HOME = "${pkgs.jdk21}";
            JAVA_DEBUG_BUNDLE = "${pkgs.vscode-extensions.vscjava.vscode-java-debug}/share/vscode/extensions/vscjava.vscode-java-debug/server/com.microsoft.java.debug.plugin-*.jar";
            JAVA_TOOL_OPTIONS = "--enable-native-access=ALL-UNNAMED";

            LD_LIBRARY_PATH = builtins.concatStringsSep ":" [
              "/run/opengl-driver/lib"
              (pkgs.lib.makeLibraryPath graphicsLibraries)
            ];

            LIBGL_DRIVERS_PATH = builtins.concatStringsSep ":" [
              "/run/opengl-driver/lib/dri"
              "${pkgs.mesa}/lib/dri"
            ];
          };
        }
      );
    };
}
