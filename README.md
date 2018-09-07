# bq
Bootique CLI tool

## Homebrew local install
 * Clone repository
 ```
 git clone https://github.com/bootique-tools/bq.git
 ```
 * Build with maven
 ```
 cd bootique-shiro-demo
 mvn clean install
 ```

 * Add Formula to homebrew
 ```
 cp ./target/homebrew/bootique.rb /usr/local/Homebrew/Library/Taps/pivotal/homebrew-tap
 ```

 * Run the brew installation
 ```
 brew install bootique
 ```

## Run the bootique cli

 * cd to your empty dirrectory and run command
 ```
 bq -n --hello-tpl
 ```
