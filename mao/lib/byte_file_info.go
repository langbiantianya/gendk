package lib

import (
	"io/fs"
	"time"
)

// ByteFileInfo 仅基于 []byte 实现的 fs.FileInfo
type ByteFileInfo struct {
	data     []byte      // 关联的字节数组
	filename string      // 自定义文件名（默认 "unknown.txt"）
	mode     fs.FileMode // 文件模式（默认 0644，常规文件权限）
	modTime  time.Time   // 修改时间（默认当前时间）
}

// 实现 fs.FileInfo 必需的 6 个方法
func (b *ByteFileInfo) Name() string       { return b.filename }
func (b *ByteFileInfo) Size() int64        { return int64(len(b.data)) } // 唯一真实值
func (b *ByteFileInfo) Mode() fs.FileMode  { return b.mode }
func (b *ByteFileInfo) ModTime() time.Time { return b.modTime }
func (b *ByteFileInfo) IsDir() bool        { return false } // 非目录
func (b *ByteFileInfo) Sys() any           { return nil }   // 无系统信息

// NewByteFileInfo 便捷构造函数：仅传入 []byte，其他用默认值
func NewByteFileInfo(data []byte, filename string) *ByteFileInfo {
	return &ByteFileInfo{
		data:     data,
		filename: filename,   // 默认文件名
		mode:     0644,       // 默认权限：用户可读写，其他只读
		modTime:  time.Now(), // 默认修改时间：当前时间
	}
}
