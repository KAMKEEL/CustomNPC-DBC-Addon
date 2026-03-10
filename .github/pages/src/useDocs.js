import { useState, useEffect } from 'react'

const REPO_OWNER = 'kamkeel'
const REPO_NAME  = 'CustomNPC-DBC-Addon'
const BASE_PATH  = `/${REPO_NAME}`
const API_BASE   = `https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}`

async function fetchFromManifest() {
  const res = await fetch(`${BASE_PATH}/manifest.json`)
  if (!res.ok) throw new Error('No manifest')
  return res.json()
}

async function fetchFromGitHubAPI() {
  const refRes = await fetch(`${API_BASE}/git/ref/heads/gh-pages`)
  if (!refRes.ok) throw new Error('gh-pages branch not found')
  const refData = await refRes.json()
  const sha = refData.object.sha

  const treeRes = await fetch(`${API_BASE}/git/trees/${sha}?recursive=1`)
  if (!treeRes.ok) throw new Error('Could not fetch tree')
  const treeData = await treeRes.json()

  const commitsRes = await fetch(`${API_BASE}/commits?sha=gh-pages&per_page=100`)
  const commits = commitsRes.ok ? await commitsRes.json() : []

  const commitMap = {}
  commits.forEach(c => {
    const date = c.commit?.committer?.date || c.commit?.author?.date || ''
    const hash = c.sha?.slice(0, 7) || ''
    const msg  = c.commit?.message || ''
    if (msg.includes('releases/latest')) commitMap['releases/latest'] = { date, hash }
    const vMatch = msg.match(/releases\/([\d.]+)/)
    if (vMatch) commitMap[`releases/${vMatch[1]}`] = { date, hash }
    const eMatch = msg.match(/experimental\/([\w-]+)/)
    if (eMatch) commitMap[`experimental/${eMatch[1]}`] = { date, hash }
  })

  const releaseDirs     = new Set()
  const experimentalDirs = new Set()

  treeData.tree.forEach(item => {
    if (item.type !== 'tree') return
    const parts = item.path.split('/')
    if (parts[0] === 'releases'     && parts.length === 2) releaseDirs.add(parts[1])
    if (parts[0] === 'experimental' && parts.length === 2) experimentalDirs.add(parts[1])
  })

  const releases = []
  if (releaseDirs.has('latest')) {
    const c = commitMap['releases/latest'] || {}
    releases.push({ id: 'latest', version: 'latest', path: 'releases/latest', date: c.date, hash: c.hash })
  }
  ;[...releaseDirs]
    .filter(v => v !== 'latest')
    .sort((a, b) => b.localeCompare(a, undefined, { numeric: true }))
    .forEach(v => {
      const c = commitMap[`releases/${v}`] || {}
      releases.push({ id: v, version: v, path: `releases/${v}`, date: c.date, hash: c.hash })
    })

  const experimental = [...experimentalDirs].sort().map(b => {
    const c = commitMap[`experimental/${b}`] || {}
    return { id: b, branch: b, path: `experimental/${b}`, date: c.date, hash: c.hash }
  })

  return { releases, experimental, source: 'api' }
}

export function useDocs() {
  const [data,    setData]    = useState(null)
  const [loading, setLoading] = useState(true)
  const [error,   setError]   = useState(null)

  useEffect(() => {
    fetchFromManifest()
      .then(d => { setData({ ...d, source: 'manifest' }); setLoading(false) })
      .catch(() =>
        fetchFromGitHubAPI()
          .then(d => { setData(d); setLoading(false) })
          .catch(e => { setError(e.message); setLoading(false) })
      )
  }, [])

  return { data, loading, error }
}

export { BASE_PATH, REPO_OWNER, REPO_NAME }
