import { useState, useEffect } from 'react'
import styles from './TerminalHeader.module.css'

const LINKS = [
  {
    tag: 'GH',
    label: 'github.com/kamkeel/CustomNPC-DBC-Addon',
    href: 'https://github.com/kamkeel/CustomNPC-DBC-Addon',
    color: '#e6edf3',
  },
  {
    tag: 'DC',
    label: 'discord.gg/pQqRTvFeJ',
    href: 'https://discord.com/invite/pQqRTvFeJ',
    color: '#5865f2',
  },
  {
    tag: 'CF',
    label: 'curseforge.com/minecraft/mc-mods/cnpc-dbc-addon',
    href: 'https://www.curseforge.com/minecraft/mc-mods/cnpc-dbc-addon',
    color: '#f16436',
  },
  {
    tag: 'MR',
    label: 'modrinth.com/mod/customnpc-plus-dbc-addon',
    href: 'https://modrinth.com/mod/customnpc-plus-dbc-addon/',
    color: '#1bd96a',
  },
]


// Add more GitHub usernames here as the project grows
const AUTHORS = ['somehussar', 'kamkeel', 'bigguy345']

function useGitHubProfile(username) {
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch(`https://api.github.com/users/${username}`)
      .then(r => r.json())
      .then(d => { setProfile(d); setLoading(false) })
      .catch(() => setLoading(false))
  }, [username])

  return { profile, loading }
}

function AuthorCard({ username }) {
  const { profile, loading } = useGitHubProfile(username)

  if (loading) {
    return (
      <div className={styles.authorCard}>
        <div className={styles.authorAvatarSkeleton} />
        <div className={styles.authorInfo}>
          <div className={styles.authorSkeleton} style={{ width: '80px' }} />
          <div className={styles.authorSkeleton} style={{ width: '120px', marginTop: '4px' }} />
        </div>
      </div>
    )
  }

  if (!profile || profile.message) {
    return (
      <div className={styles.authorCard}>
        <div className={styles.authorAvatarSkeleton} />
        <div className={styles.authorInfo}>
          <span className={styles.authorName}>{username}</span>
        </div>
      </div>
    )
  }

  return (
    <a
      href={profile.html_url}
      target="_blank"
      rel="noopener noreferrer"
      className={`${styles.authorCard} ${styles.authorCardLink}`}
    >
      <img
        src={profile.avatar_url}
        alt={profile.login}
        className={styles.authorAvatar}
      />
      <div className={styles.authorInfo}>
        <span className={styles.authorName}>{profile.name || profile.login}</span>
        <span className={styles.authorLogin}>@{profile.login}</span>
        {profile.bio && (
          <span className={styles.authorBio}>{profile.bio}</span>
        )}
      </div>
      <span className={styles.authorArrow}>↗</span>
    </a>
  )
}

function AuthorsCarousel() {
  const [current, setCurrent] = useState(0)

  useEffect(() => {
    if (AUTHORS.length <= 1) return
    const t = setInterval(() => {
      setCurrent(i => (i + 1) % AUTHORS.length)
    }, 4000)
    return () => clearInterval(t)
  }, [])

  return (
    <div className={styles.authors}>
      <div className={styles.outputLine}>
        <span className={styles.chevron}>›</span> Authors:
      </div>
      {AUTHORS.length > 1 && (
              <div className={styles.carouselDots}>
                {AUTHORS.map((_, i) => (
                  <button
                    key={i}
                    className={`${styles.dot2} ${i === current ? styles.dot2Active : ''}`}
                    onClick={() => setCurrent(i)}
                  />
                ))}
              </div>
            )}
      <div className={styles.carouselWrap}>
        {AUTHORS.map((username, i) => (
          <div
            key={username}
            className={`${styles.carouselSlide} ${i === current ? styles.carouselActive : ''}`}
          >
            <AuthorCard username={username} />
          </div>
        ))}
      </div>
    </div>
  )
}

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
        <h1 className={styles.heading}>
        Custom<strong>NPC+</strong>&nbsp;DBC&nbsp;Addon
{/*         <span className={styles.cursor} /> */}
        </h1>
        <div className={styles.promptLine}>
          <span className={styles.prompt}>$</span>
          <span className={styles.cmd}>browse-docs</span>
          <span className={styles.arg}>CustomNPC-DBC-Addon</span>
          <span className={styles.flag}>--all-versions</span>
        </div>

        <div className={styles.outputLine}>
          <span className={styles.chevron}>›</span> Fetching available documentation builds...
        </div>
        <div className={styles.outputLine}>
          <span className={styles.chevron}>›</span> Found project links:
        </div>

        <div className={styles.links}>
          {LINKS.map(({ tag, label, href, color }) => (
            <a
              key={tag}
              href={href}
              target="_blank"
              rel="noopener noreferrer"
              className={styles.link}
            >
              <span className={styles.linkTag} style={{ color, borderColor: color }}>
                {tag}
              </span>
              <span className={styles.linkLabel}>{label}</span>
              <span className={styles.linkArrow} style={{ color }}>↗</span>
            </a>
          ))}
        </div>

        <AuthorsCarousel />

        <div className={styles.promptLine} style={{ marginTop: '16px' }}>
          <span className={styles.prompt}>$</span>
          <span className={styles.cursor} />
        </div>
      </div>
    </div>
  )
}
