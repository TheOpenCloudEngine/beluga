#!/bin/bash
#
# Docker Toolbox 가 설치된 (https://www.docker.com/products/docker-toolbox) Mac os x 에서
# Virtual Machine 을 이용하여 도커를 부팅시킨 후, docker client 가 virtual box docker 를 사용할 수 있도록 env 를 설정한다.
# @author : Seungpil, Park
#

VM=default
DOCKER_MACHINE=/usr/local/bin/docker-machine
VBOXMANAGE=/Applications/VirtualBox.app/Contents/MacOS/VBoxManage

BLUE='\033[0;34m'
GREEN='\033[0;32m'
NC='\033[0m'

unset DYLD_LIBRARY_PATH
unset LD_LIBRARY_PATH

clear

if [ ! -f "${DOCKER_MACHINE}" ] || [ ! -f "${VBOXMANAGE}" ]; then
  echo "Either VirtualBox or Docker Machine are not installed. Please re-run the Toolbox Installer and try again."
  exit 1
fi

"${VBOXMANAGE}" list vms | grep \""${VM}"\" &> /dev/null
VM_EXISTS_CODE=$?

if [ $VM_EXISTS_CODE -eq 1 ]; then
  "${DOCKER_MACHINE}" rm -f "${VM}" &> /dev/null
  rm -rf ~/.docker/machine/machines/"${VM}"
  "${DOCKER_MACHINE}" create -d virtualbox --virtualbox-memory 2048 --virtualbox-disk-size 204800 "${VM}"
fi

VM_STATUS="$(${DOCKER_MACHINE} status ${VM} 2>&1)"
if [ "${VM_STATUS}" != "Running" ]; then
  "${DOCKER_MACHINE}" start "${VM}"
  yes | "${DOCKER_MACHINE}" regenerate-certs "${VM}"
fi

eval "$(${DOCKER_MACHINE} env --shell=bash ${VM})"

clear
cat << EOF


                        ##         .
                  ## ## ##        ==
               ## ## ## ## ##    ===
           /"""""""""""""""""\___/ ===
      ~~~ {~~ ~~~~ ~~~ ~~~~ ~~~ ~ /  ===- ~~~
           \______ o           __/
             \    \         __/
              \____\_______/


EOF
echo -e "${BLUE}docker${NC} is configured to use the ${GREEN}${VM}${NC} machine with IP ${GREEN}$(${DOCKER_MACHINE} ip ${VM})${NC}"
echo "For help getting started, check out the docs at https://docs.docker.com"
echo

USER_SHELL="$(dscl /Search -read /Users/${USER} UserShell | awk '{print $2}' | head -n 1)"
if [[ "${USER_SHELL}" == *"/bash"* ]] || [[ "${USER_SHELL}" == *"/zsh"* ]] || [[ "${USER_SHELL}" == *"/sh"* ]]; then
  "${USER_SHELL}" --login
else
  "${USER_SHELL}"
fi