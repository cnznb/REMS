public class old{
    private Cursor getIdentityKeyCursor(Account account, String name, boolean own) {
		final SQLiteDatabase db = this.getReadableDatabase();
		String[] columns = {AxolotlService.SQLiteAxolotlStore.KEY};
		String[] selectionArgs = {account.getUuid(),
				name,
				own?"1":"0"};
		Cursor cursor = db.query(AxolotlService.SQLiteAxolotlStore.IDENTITIES_TABLENAME,
				columns,
				AxolotlService.SQLiteAxolotlStore.ACCOUNT + " = ? AND "
						+ AxolotlService.SQLiteAxolotlStore.NAME + " = ? AND "
						+ AxolotlService.SQLiteAxolotlStore.OWN + " = ? ",
				selectionArgs,
				null, null, null);

		return cursor;
	}
}
