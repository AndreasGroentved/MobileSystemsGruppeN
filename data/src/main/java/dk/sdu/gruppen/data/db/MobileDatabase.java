package dk.sdu.gruppen.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import dk.sdu.gruppen.data.Model.RawNode;

/**
 * Created by Andreas Grøntved on 15-12-2017.
 **/

@Database(entities = {RawNode.class}, version = 1)
public abstract class MobileDatabase extends RoomDatabase {
    public abstract RawNodeDao rawNodeDao();
}
