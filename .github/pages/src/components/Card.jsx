import styles from './Card.module.css'
import { BASE_PATH } from '../useDocs'

function formatDate(iso) {
  if (!iso) return null
  try {
    return new Intl.DateTimeFormat('en-GB', {
      day: '2-digit', month: 'short', year: 'numeric'
    }).format(new Date(iso))
  } catch { return null }
}

export function ReleaseCard({ release, index }) {
  const isLatest = release.isLatest  // comes from manifest, not inferred
  const date     = formatDate(release.date)

  return (
    <a
      href={`${BASE_PATH}/${release.path}/`}
      className={`${styles.card} ${isLatest ? styles.latest : styles.release}`}
      style={{ animationDelay: `${index * 50}ms` }}
    >
      <div className={styles.header}>
        <span className={`${styles.badge} ${isLatest ? styles.badgeLatest : styles.badgeRelease}`}>
          {isLatest ? '● latest' : 'release'}
        </span>
        <span className={styles.arrow}>→</span>
      </div>

      <div className={styles.version}>
        v{release.version}
        {isLatest && <span className={styles.latestPill}>latest</span>}
      </div>

      <div className={styles.meta}>
        {date && (
          <div className={styles.metaRow}>
            <span className={styles.metaKey}>date</span>
            <span className={styles.metaVal}>{date}</span>
          </div>
        )}
        {release.hash && (
          <div className={styles.metaRow}>
            <span className={styles.metaKey}>sha</span>
            <span className={`${styles.metaVal} ${styles.hash}`}>{release.hash}</span>
          </div>
        )}
        <div className={styles.metaRow}>
          <span className={styles.metaKey}>path</span>
          <span className={styles.metaVal}>{release.path}/</span>
        </div>
      </div>
    </a>
  )
}

export function ExperimentalCard({ branch, index }) {
  const date = formatDate(branch.date)

  return (
    <a
      href={`${BASE_PATH}/${branch.path}/`}
      className={`${styles.card} ${styles.experimental}`}
      style={{ animationDelay: `${index * 50}ms` }}
    >
      <div className={styles.header}>
        <span className={`${styles.badge} ${styles.badgeExperimental}`}>
          experimental
        </span>
        <span className={styles.arrow}>→</span>
      </div>

      <div className={`${styles.version} ${styles.versionBranch}`}>
        {branch.branch}
      </div>

      <div className={styles.meta}>
        {date && (
          <div className={styles.metaRow}>
            <span className={styles.metaKey}>date</span>
            <span className={styles.metaVal}>{date}</span>
          </div>
        )}
        {branch.hash && (
          <div className={styles.metaRow}>
            <span className={styles.metaKey}>sha</span>
            <span className={`${styles.metaVal} ${styles.hash}`}>{branch.hash}</span>
          </div>
        )}
        <div className={styles.metaRow}>
          <span className={styles.metaKey}>path</span>
          <span className={styles.metaVal}>{branch.path}/</span>
        </div>
      </div>
    </a>
  )
}
