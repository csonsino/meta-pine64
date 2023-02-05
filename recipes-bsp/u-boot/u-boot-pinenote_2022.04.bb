
require recipes-bsp/u-boot/u-boot.inc

HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
DESCRIPTION = "U-Boot, a boot loader for Embedded boards based on PowerPC, \
ARM, MIPS and several other processors, which can be installed in a boot \
ROM and used to initialize and test the hardware or to download and run \
application code."
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=5a7450c57ffe5ae63fd732446b988025"
PE = "1"

SRCREV = "ae59228edff4cb3d4abb1b44b470fafa352601a7"
SRC_URI = "git://gitlab.com/pgwipeout/u-boot-quartz64.git;branch=quartz64"

SRC_URI += " \
    file://boot.cmd \
"

RAM_INIT_BIN = "rk3568_ddr_1560MHz_v1.13.bin"
BL31_BIN = "rk3568_bl31_v1.28.elf"

# Extra rockchip binaries
# NOTE: Advised to use specific versions of the binaries
SRC_URI += " \
    https://github.com/JeffyCN/rockchip_mirrors/raw/rkbin/tools/mkimage;name=mkimage \
    https://github.com/JeffyCN/rockchip_mirrors/raw/6186debcac95553f6b311cee10669e12c9c9963d/bin/rk35/${BL31_BIN};name=bl_bin \
    https://github.com/JeffyCN/rockchip_mirrors/raw/ddf03c1d80b33dac72a33c4f732fc5849b47ff99/bin/rk35/${RAM_INIT_BIN};name=ram_init_bin \
"

SRC_URI[mkimage.md5sum] = "12a8539636a728e6c1440690d2445a38"
SRC_URI[mkimage.sha256sum] = "5bf637875b00327eab6f0d08b7ce4d5451e92bdecd6912a002fa4484ae98bca7"
SRC_URI[bl_bin.md5sum] = "144014c510a90309e9e1a40a93b32c82"
SRC_URI[bl_bin.sha256sum] = "67bf19566fb646e2f1f55b7fbf084f0d71b59b875a19a077e638b95adf1b254a"
SRC_URI[ram_init_bin.md5sum] = "3226edad13d85780e0689bfc4253123e"
SRC_URI[ram_init_bin.sha256sum] = "53d5e893916e647ccb8c5a2a51f749e9e11bf7329e61a2f94d8c089a333d7812"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

DEPENDS:append:pinenote-a55 = " bc-native dtc-native python3-setuptools-native u-boot-tools-native"

SPL_BINARY="u-boot-rockchip.bin"
UBOOT_MACHINE = "pinenote-rk3566_defconfig"
UBOOT_ENV_SUFFIX = "scr"
UBOOT_ENV = "boot"

do_configure:append:pinenote-a55() {
    # Copy binaries where they need to be
    install -m 755 ${WORKDIR}/mkimage ${S}/tools/
    install -m 644 ${WORKDIR}/${RAM_INIT_BIN} ${B}/ram_init.bin
    install -m 644 ${WORKDIR}/${BL31_BIN} ${B}/bl31.elf
}

do_compile:append() {
    # Translate boot.txt to a boot.scr by using the mkimage command 
    ${S}/tools/mkimage -C none -A arm -T script -d ${WORKDIR}/boot.cmd ${WORKDIR}/${UBOOT_ENV_BINARY}
}
