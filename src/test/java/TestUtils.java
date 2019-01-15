import warehouse.Database;

import java.sql.SQLException;

class TestUtils {

    static void initDB() throws SQLException {
        try {
            Database.getInstance().initTestDB();
        } catch (SQLException e) {
            Database.getInstance().deleteTestDB();
            Database.getInstance().initTestDB();
        }
    }

    static void deleteDB() {
        try {
            Database.getInstance().deleteTestDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
