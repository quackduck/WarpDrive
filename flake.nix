{
  description = "Warp across directories";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }: flake-utils.lib.eachDefaultSystem (system: let
    pkgs = nixpkgs.legacyPackages.${system};
    pname = "wd";
  in rec {
    packages.wd = pkgs.stdenv.mkDerivation {
      name = pname;
      src = ./.;

      nativeBuildInputs = [ pkgs.jdk pkgs.makeWrapper ];

      buildPhase = ''
        mkdir -p $out/share/${pname}
        javac -d $out/share/${pname} $src/src/WarpDrive.java
      '';

      installPhase = ''
        mkdir -p $out/bin
        makeWrapper ${pkgs.jre}/bin/java $out/bin/wd \
          --add-flags "-cp $out/share/${pname} WarpDrive"
        cp -a $src/man $out/man
      '';

      meta = with pkgs.lib; {
        description = "Warp across directories";
        homepage = "https://github.com/quackduck/WarpDrive";
        license = licenses.mit;
        platforms = platforms.linux ++ platforms.darwin;
      };
    };
    defaultPackage = packages.wd;
  });
}
