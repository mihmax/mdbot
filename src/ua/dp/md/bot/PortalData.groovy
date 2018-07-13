package ua.dp.md.bot

class PortalData {
    int portalOrder
    String portalName
    String coordinates

    PortalData(int portalOrder, String portalName, String coordinates) {
        this.portalOrder = portalOrder
        this.portalName = portalName
        this.coordinates = coordinates
    }

    String toString() {
        "$portalOrder) $portalName @ $coordinates"
    }

    String intelLink() {
        "https://www.ingress.com/intel?ll=${coordinates}&z=17&pll=${coordinates}"
    }
    String googleMapLink() {
        "https://www.google.com/maps/place/${coordinates}"
    }
}
