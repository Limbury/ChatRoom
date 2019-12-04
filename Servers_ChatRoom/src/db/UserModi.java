package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tools.ConTool;

public class UserModi {
	public UserInfo getUserByUID(int UID) throws SQLException {
		ConTool contool=new ConTool();
        ResultSet rs = contool.query("SELECT * FROM userinfo where uid=" + UID);
        if (!rs.next()){
        	contool.release();
        	return null;
        }
        UserInfo user = new UserInfo(rs);

        //Get Friend List
        FriendModi friendmodi = new FriendModi();
        List<FriendInfo> coll = friendmodi.getCollectionsByUID(UID);
        FriendInfo collection;
        List<UserInfo> memberlist;
        UserInfo member;

        int collectionCount = coll.size();
        int memberCount = 0;
        user.setCollectionCount((byte) collectionCount);

        String[] ListName = new String[collectionCount];
        byte[] bodyCount = new byte[collectionCount];// Ã¿ï¿½ï¿½ï¿½Ð¶ï¿½ï¿½Ù¸ï¿½ï¿½ï¿½
        int bodyNum[][] = new int[collectionCount][];// Ã¿ï¿½ï¿½ï¿½ï¿½ï¿½Ñµï¿½JKï¿½ï¿½
        int bodypic[][] = new int[collectionCount][];//ï¿½ï¿½ï¿½ï¿½Í·ï¿½ï¿½
        String bodyName[][] = new String[collectionCount][];// Ã¿ï¿½ï¿½ï¿½ï¿½ï¿½Ñµï¿½ï¿½Ç³ï¿½

        for (int j = 0; j < coll.size(); j++) {
            try {
                collection = coll.get(j);
                ListName[j] = collection.getName();
                memberlist = collection.getMembers();

                memberCount = memberlist.size();
                bodyCount[j] = (byte) memberCount;

                bodyNum[j] = new int[memberCount];
                bodyName[j] = new String[memberCount];
                bodypic[j] = new int[memberCount];

                for (int i = 0; i < memberlist.size(); i++) {
                    member = memberlist.get(i);
                    bodyNum[j][i] = member.getUID();
                    bodyName[j][i] = member.getNickName();
                    bodypic[j][i] = member.getAvatar();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //set friend list
        user.setBodyName(bodyName);
        user.setListName(ListName);
        user.setBodyCount(bodyCount);
        user.setBodyNum(bodyNum);
        user.setBodypic(bodypic);

        contool.release();
        return user;
    }

    /**
     * userAuthorization
     * ï¿½ï¿½Ö¤ï¿½Ã»ï¿½ï¿½ï¿½ï¿½ï¿½
     *
     * @param jk
     * @param passwd
     * @return boolean Result
     * @throws SQLException
     * @author Hcyue
     */
    public boolean userAuthorization(int uid, String passwd) throws SQLException {
    	boolean res=false;
    	ConTool contool=new ConTool();
        ResultSet rs = contool.query(String.format("SELECT * FROM userinfo WHERE uid=%d AND pwd='%s'", uid, passwd));
        if (rs.next()) 
        	res=true;
        contool.release();
        return res;
    }

    public boolean isFriendsOfUser(int target, int jk) throws SQLException {
    	boolean res=false;
        String sql = String.format(
                "SELECT\n" +
                        "	*\n" +
                        "FROM\n" +
                        "	userinfo\n" +
                        "WHERE\n" +
                        "	uid = %d\n" +
                        "AND userinfo.uid IN (\n" +
                        "	SELECT\n" +
                        "		uid\n" +
                        "	FROM\n" +
                        "		relationship_1\n" +
                        "	WHERE\n" +
                        "		lid IN (\n" +
                        "			SELECT\n" +
                        "				lid\n" +
                        "			FROM\n" +
                        "				uid_lid\n" +
                        "			WHERE\n" +
                        "				uid = %d\n" +
                        "		)\n" +
                        ")", target, jk);
        ConTool contool=new ConTool();
        ResultSet rs = contool.query(sql);
        if(rs.next())
        	res=true;
        contool.release();
        return res;
    }

    /**
     * getUsersInCollection
     *
     * @param coll_id
     * @return
     * @throws SQLException
     * @author Hcyue
     */
    public List<UserInfo> getUsersInCollection(int coll_id) throws SQLException {
    	ConTool contool=new ConTool();
        ResultSet rs = contool.query("SELECT * FROM userinfo WHERE uid IN (SELECT uid FROM relationship_1 WHERE lid = " + coll_id + ")");
        ArrayList<UserInfo> res = new ArrayList<>();
        while (rs.next()) {
            res.add(new UserInfo(rs));
        }
        contool.release();
        return res;
    }

    /**
     * createUser
     * ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
     *
     * @param passwd
     * @param nick
     * @param avatar
     * @return
     * @throws SQLException
     */
    public UserInfo createUser(String passwd, String nick, int avatar) throws SQLException {
        String sql = String.format("INSERT INTO userinfo (nickname, pwd, avatar) VALUES ('%s', '%s', %d)", nick, passwd, avatar);
        ConTool contool=new ConTool();
        int res = contool.insertAndGet(sql);
        return getUserByUID(res);
    }

    /**
     * removeUser
     * É¾ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
     *
     * @param jk
     * @return
     * @throws SQLException
     */
    public int removeUser(int uid) throws SQLException {
        String sql = String.format("DELETE FROM userinfo WHERE uid=%d", uid);
        ConTool contool=new ConTool();
        int res =contool.update(sql);
        return res;
    }

    /**
     * @param addJk
     * @param ownJk
     * @param listName
     * @return 0 ³É¹¦
     * @throws Exception
     * @author He11o_Liu
     */
    public int addFriend(int addJk, int ownJk, String listName) throws Exception {
        //check add_jk
        UserInfo dest = getUserByUID(addJk);
        if (dest == null) {
            //²»´æÔÚÕâ¸öÈË
            return 1;
        }
        FriendModi collectionModel = new FriendModi();
        FriendInfo collection = collectionModel.getCollectionByNameAndOwner(listName, ownJk);
        if (collection == null) {
            collection = collectionModel.createCollection(ownJk, listName);
        } else if (isFriendsOfUser(addJk, ownJk)) {
            return 2;
        }
        collectionModel.addUserToCollection(addJk, collection.getId());
        return 0;
    }
}
