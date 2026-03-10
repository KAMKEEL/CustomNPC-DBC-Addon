import styles from './TerminalHeader.module.css'

export default function TerminalHeader() {
  return (
    <div className={styles.wrap}>
      <div className={styles.bar}>
        <div className={`${styles.dot} ${styles.red}`}   />
        <div className={`${styles.dot} ${styles.amber}`} />
        <div className={`${styles.dot} ${styles.green}`} />
        <span className={styles.title}>javadoc-index — bash</span>
      </div>
      <div className={styles.body}>
        <div className={styles.promptLine}>
          <span className={styles.prompt}>$</span>
          <span className={styles.cmd}>browse-docs</span>
          <span className={styles.arg}>CustomNPC-DBC-Addon</span>
          <span className={styles.flag}>--all-versions</span>
        </div>
        <div className={styles.outputLine}>
          <span>›</span> Fetching available documentation builds...
        </div>
        <h1 className={styles.heading}>
          Custom<strong>NPC+</strong>
          <br />
          DBC&nbsp;Addon
          <span className={styles.cursor} />
        </h1>
      </div>
    </div>
  )
}
