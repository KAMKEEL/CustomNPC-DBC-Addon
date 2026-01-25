/**
 * DBC Addon patch for IPlayer
 * @javaFqn noppes.npcs.api.entity.IPlayer
 */
export interface IPlayer<T> {
  getDBCPlayer(): IDBCAddon;
}
