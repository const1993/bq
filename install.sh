#!/usr/bin/env bash

VERSION="1.0-SNAPSHOT"
BASE_DIR="${HOME}/bq"
ARCHIVE_DIR="${BASE_DIR}/archives"
TMP_DIR="${BASE_DIR}/tmp"
ARCHIVE="${ARCHIVE_DIR}/bq-${VERSION}.zip"
DESTINATION_DIR="$BASE_DIR/$CANDIDATE-$VERSION"

bq_shourtcut=$( cat << EOF
alias bq='java -jar "/Users/haria/.bq/lib/bq-1.0-SNAPSHOT.jar" $*'
EOF
)


echo '                                                                            '
echo '                                                                            '
echo '                                                                            '
echo '                                                                            '
echo '                                    +++++++++++:                            '
echo '                                   +++++    +++++                           '
echo '                                 :+++++      ++++;                          '
echo '                                +++++++      :++++                          '
echo '                               ;++,+++`       ++++                          '
echo '                               :+::+++       `++++                          '
echo '                                  ++++        ++++                          '
echo '                                  +++,       ++++                           '
echo '                                  +++        ++++                           '
echo '                                 ++++       ++++                            '
echo '                                 +++       .+++;                            '
echo '                                 +++       ++++                             '
echo '                                ,+++      ++++                              '
echo '                                ++++  . +++++                               '
echo '                                ++,++++++++++.                              '
echo '                                +++++++++  .++,                             '
echo '                               :+++ ++`    ++++                             '
echo '                               ++++         ++++                            '
echo '                               ++++         ++++                            '
echo '                               +++`         ++++                            '
echo '                              ++++          ++++                            '
echo '                              ++++         ;++++                            '
echo '                              ++++         ++++,                            '
echo '                             ,+++:        ,++++                             '
echo '                             ++++         ++++                              '
echo '                             ++++        ++++.                              '
echo '                             ++++      `++++;                               '
echo '                            :++++     +++++.                                '
echo '                             ++++++++++++:                                  '
echo '                            `++++++++++`                                    '
echo '                                         .;++++,                            '
echo '                                    +++++++++++,                            '
echo '                               :+++++++++++ ,`                              '
echo '                             +++++++++:                                     '
echo '                             ++++++                                         '
echo '                                                                            '
echo '                                                                            '
echo '                                                                            '
echo '                                                                            '

if [[ -d "${BASE_DIR}/${VERSION}" || -h "${BASE_DIR}/${VERSION}" ]]; then
    echo "bq ${VERSION} already exist"
    return 0
fi

if [ -z "${ARCHIVE}" ]; then
    echo "no zip, try load it"
fi

unzip -qo "${ARCHIVE}" -d "${BASE_DIR}/${VERSION}"

Move jar and sh script from temp folder to destination folder
Edit .bash_profile file (create if not exist, add new shourtcut if not exist)