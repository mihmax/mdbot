package ua.dp.md.bot

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord

import static ua.dp.md.bot.Language.EN
import static ua.dp.md.bot.Language.RU
import static ua.dp.md.bot.Language.UK

class MissionData {

    private File directory
    int number
    Map<Language, String> title
    Map<Language, String> description
    List<PortalData> portals

    MissionData(File directory, int number, Map<Language, String> title, Map<Language, String> description, List<PortalData> portals) {
        this.directory = directory
        this.number = number
        this.title = title
        this.description = description
        this.portals = portals

        if (!photo.exists()) throw new RuntimeException("File ${photo.absolutePath} not found on disk, cannot start")
        if (!map.exists()) throw new RuntimeException("File ${map.absolutePath} not found on disk, cannot start")
    }

    File getPhoto() {
        return new File(directory, "${number}.jpg")
    }
    File getMap() {
        return new File(directory, "map${number}.png")
    }

    String toString() {
        "$number) $title ($description) $portals"
    }

    static List<MissionData> readMissions(File directory) {
        List<MissionData> missions = new ArrayList<>()

        def csvReader = new FileReader(new File(directory, 'missions.csv'))
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(csvReader)

        // 0       1             2              3                 4
        // M. No., Mission Type, Mission Title, UK Mission Title, RU Mission Title,
        // 5                    6               7
        // Mission Description, UK Description, RU Description,
        // 8      9            10          11         12          13        14          15
        // Order, Portal Name, Portal URL, Objective, Passphrase, Question, Coordinate, Note
        for(CSVRecord row in records) {
            def missionNumberString = row.get(0)
            try {
                int missionNumber = missionNumberString.toInteger()
                if (missions.size() >= missionNumber) {
                    missions.get(missionNumber - 1).portals.add(new PortalData(
                            row.get(8).toInteger(),
                            row.get(9).trim(),
                            row.get(14).trim()
                    ))
                } else {
                    MissionData mission = new MissionData(
                            directory,
                            missionNumber,
                            [(EN): row.get(2).trim(), (UK): row.get(3).trim(), (RU): row.get(4).trim()],
                            [(EN): row.get(5).trim(), (UK): row.get(6).trim(), (RU): row.get(7).trim()],
                            [new PortalData(
                                    row.get(8).toInteger(),
                                    row.get(9).trim(),
                                    row.get(14).trim()
                            )]
                    )
                    missions.add(mission)
                }
            } catch (NumberFormatException ignored) {
                // fine
                println 'CANNOT PARSE:'
                println row
            }
        }

        csvReader.close()

        missions
    }
}
