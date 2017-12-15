package dk.sdu.gruppen.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import dk.sdu.gruppen.data.Model.RawNode;

/**
 * Created by Andreas Grøntved on 15-12-2017.
 **/

@Dao
public interface RawNodeDao {

    @Query("SELECT * FROM RawNode")
    List<RawNode> getAllNodes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertRawNodes(RawNode[] rawnodes);

    @Query("DELETE FROM RawNode")
    public void emptyTable();
}
