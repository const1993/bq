require 'formula'

class Bootique < Formula
  homepage 'https://bootique.io/bootique-tools/'
  url 'file:///${project.build.directory}/bq-${project.version}-bin.tar.gz'
  version '${project.version}'
  sha256 '${checksum}'
  head 'https://github.com/bootique-tools/bq.git'

  if build.head?
    depends_on 'maven' => :build
  end

  def install
    if build.head?
      Dir.chdir('bq') { system 'mvn clean -U -DskipTests=true package' }
      root = 'bq/target/bq-*-bin/bq-*'
    else
      root = '.'
    end

    bin.install Dir["#{root}/bin/bq"]
    lib.install Dir["#{root}/lib/bq-*.jar"]
  end
end