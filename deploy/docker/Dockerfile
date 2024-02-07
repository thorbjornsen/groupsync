#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

ARG BASE_IMAGE
FROM ${BASE_IMAGE}
USER root
RUN whoami

RUN microdnf -y install shadow-utils
RUN \
    set -xeu && \
    groupadd groupsync --gid 1000 && \
    useradd groupsync --uid 1000 --gid 1000 && \
    mkdir -p /usr/lib/groupsync && \
    chown -R "groupsync:groupsync" /usr/lib/groupsync

ARG GROUPSYNC_VERSION
COPY --chown=groupsync:groupsync main/target/main-${GROUPSYNC_VERSION}-jar-with-dependencies.jar /usr/lib/groupsync/groupsync-${GROUPSYNC_VERSION}.jar

USER groupsync:groupsync
ENV LANG en_US.UTF-8
ENV GROUPSYNC_VERSION ${GROUPSYNC_VERSION}
ENTRYPOINT java -Dconfig=/etc/starburst/config.properties -jar /usr/lib/groupsync/groupsync-${GROUPSYNC_VERSION}.jar

